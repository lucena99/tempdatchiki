package ru.psv4.tempdatchiki.utils;

import ru.psv4.tempdatchiki.backend.data.IncidentType;
import ru.psv4.tempdatchiki.backend.data.IncidentTyped;
import ru.psv4.tempdatchiki.backend.data.Notification;
import ru.psv4.tempdatchiki.backend.schedulers.TempEvent;

import java.util.Optional;

import static ru.psv4.tempdatchiki.backend.data.IncidentType.*;
import static ru.psv4.tempdatchiki.backend.data.IncidentType.Error;

public class IncidentDecisionResolver {

    /**
     * Резолвит событие изменения температуры. С помощью {@code ldGetter} получает последнее принятое решение
     * по данному датчику, если его тип инцидента отличается от текущего типа инцидента, вызывается {@code maker}
     * @param event событие изменения температуры
     * @param ldGetter операция по получению последнего принятого решения по датчику
     * @param maker операция принятия решения
     * @param <D> Решение
     */
    public static <D extends IncidentTyped> void resolve(TempEvent event, LastDecisionGetter<D> ldGetter,
                                                         DecisionMaker<D> maker) {
        Optional<D> ld = ldGetter.get(event);
        IncidentType it = IncidentType.defineType(event);
        switch (it) {
            case Normal: {
                if (ld.isPresent() && ld.get().getType() != Normal) {
                    maker.make(event, it);
                }
                break;
            }
            case OverDown: {
                if (!ld.isPresent() ||
                        (ld.isPresent() && ld.get().getType() != OverDown)) {
                    maker.make(event, it);
                }
                break;
            }
            case OverUp: {
                if (!ld.isPresent() ||
                        (ld.isPresent() && ld.get().getType() != OverUp)) {
                    maker.make(event, it);
                }
                break;
            }
            case Error: {
                if (!ld.isPresent() ||
                        (ld.isPresent() && ld.get().getType() != Error)) {
                    maker.make(event, it);
                }
                break;
            }
        }
    }

    @FunctionalInterface
    public static interface LastDecisionGetter<D extends IncidentTyped> {
        public Optional<D> get(TempEvent event);
    }

    @FunctionalInterface
    public static interface DecisionMaker<D extends IncidentTyped> {
        public void make(TempEvent event, IncidentType it);
    }
}
