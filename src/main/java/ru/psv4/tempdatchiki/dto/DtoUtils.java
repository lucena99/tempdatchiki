package ru.psv4.tempdatchiki.dto;

import ru.psv4.tempdatchiki.model.Reference;

import java.util.ArrayList;
import java.util.List;

public class DtoUtils {
    public static <R extends Reference, D extends ReferenceDto> List<D> convert(
            Class<R> rClass, Class<D> dClass, List<R> list) {
        try {
            List<D> result = new ArrayList<D>();
            for (R r : list) {
                D dto = dClass.newInstance();
                dto.setUid(r.getUid());
                dto.setName(r.getName());
                dto.setCreatedDatetime(r.getCreatedDatetime());
                result.add(dto);
            }
            return result;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public static <R extends Reference, D extends ReferenceDto> D convert(
            Class<R> rClass, Class<D> dClass, R r) {
        try {
            D dto = dClass.newInstance();
            dto.setUid(r.getUid());
            dto.setName(r.getName());
            dto.setCreatedDatetime(r.getCreatedDatetime());
            return dto;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public static <R extends Reference, D extends ReferenceDto> R createReferenceFromDto(
            Class<D> dClass, Class<R> rClass, D d) {
        try {
            R r = rClass.newInstance();
            r.setName(d.getName());
            r.setCreatedDatetime(d.getCreatedDatetime());
            return r;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
