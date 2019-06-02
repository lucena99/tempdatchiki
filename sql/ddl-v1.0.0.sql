--
-- PostgreSQL database dump
--

-- Dumped from database version 10.7
-- Dumped by pg_dump version 10.7

--
-- TOC entry 196 (class 1259 OID 24711)
-- Name: controller; Type: TABLE; Schema: public; 
--

CREATE TABLE public.controller (
    uid character varying(36) NOT NULL,
    name character varying(256) NOT NULL,
    created_datetime timestamp without time zone,
    url text NOT NULL
);

--
-- TOC entry 204 (class 1259 OID 41018)
-- Name: incident; Type: TABLE; Schema: public; 
--

CREATE TABLE public.incident (
    uid character varying(36) NOT NULL,
    sensor_uid character varying(36) NOT NULL,
    value numeric(3,1) NOT NULL,
    minvalue numeric(3,1) NOT NULL,
    maxvalue numeric(3,1) NOT NULL,
    created_datetime timestamp without time zone NOT NULL,
    type_code integer NOT NULL
);


--
-- TOC entry 197 (class 1259 OID 24717)
-- Name: notification; Type: TABLE; Schema: public; 
--

CREATE TABLE public.notification (
    uid character varying(36) NOT NULL,
    created_datetime timestamp without time zone NOT NULL,
    recipient_uid character varying(36) NOT NULL,
    sensor_uid character varying(36) NOT NULL,
    notification_code integer NOT NULL
);


--
-- TOC entry 198 (class 1259 OID 24720)
-- Name: recipient; Type: TABLE; Schema: public; 
--

CREATE TABLE public.recipient (
    uid character varying(36) NOT NULL,
    name character varying(256) NOT NULL,
    created_datetime timestamp without time zone,
    fcm_token text
);


--
-- TOC entry 199 (class 1259 OID 24726)
-- Name: sensor; Type: TABLE; Schema: public; 
--

CREATE TABLE public.sensor (
    uid character varying(36) NOT NULL,
    name character varying(256) NOT NULL,
    controller_uid character varying(36),
    created_datetime timestamp without time zone,
    minvalue numeric(3,1),
    maxvalue numeric(3,1),
    num smallint
);


--
-- TOC entry 200 (class 1259 OID 24729)
-- Name: setting; Type: TABLE; Schema: public; 
--

CREATE TABLE public.setting (
    uid character varying(36) NOT NULL,
    name character varying(256) NOT NULL,
    value text,
    created_datetime timestamp without time zone NOT NULL
);


--
-- TOC entry 201 (class 1259 OID 24735)
-- Name: subscription; Type: TABLE; Schema: public; 
--

CREATE TABLE public.subscription (
    recipient_uid character varying(36) NOT NULL,
    controller_uid character varying(36) NOT NULL,
    uid character varying(36),
    created_datetime timestamp without time zone,
    notify_over boolean,
    notify_error boolean
);


--
-- TOC entry 203 (class 1259 OID 32826)
-- Name: temp; Type: TABLE; Schema: public; 
--

CREATE TABLE public.temp (
    uid character varying(36) NOT NULL,
    sensor_uid character varying(36) NOT NULL,
    value numeric(3,1) NOT NULL,
    updated_datetime timestamp without time zone NOT NULL,
    status_code integer NOT NULL
);


--
-- TOC entry 202 (class 1259 OID 24738)
-- Name: user; Type: TABLE; Schema: public; 
--

CREATE TABLE public."user" (
    uid character varying(36) NOT NULL,
    name character varying(256) NOT NULL,
    login character varying(256) NOT NULL,
    password character varying(256) NOT NULL,
    role character varying(256) NOT NULL,
    created_datetime timestamp without time zone
);



--
-- TOC entry 2059 (class 2606 OID 24745)
-- Name: controller controller_pkey; Type: CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY public.controller
    ADD CONSTRAINT controller_pkey PRIMARY KEY (uid);


--
-- TOC entry 2067 (class 2606 OID 24747)
-- Name: sensor device_pkey; Type: CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY public.sensor
    ADD CONSTRAINT device_pkey PRIMARY KEY (uid);


--
-- TOC entry 2080 (class 2606 OID 41022)
-- Name: incident incident_pkey; Type: CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY public.incident
    ADD CONSTRAINT incident_pkey PRIMARY KEY (uid);


--
-- TOC entry 2062 (class 2606 OID 24749)
-- Name: notification message_pkey; Type: CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY public.notification
    ADD CONSTRAINT message_pkey PRIMARY KEY (uid);


--
-- TOC entry 2065 (class 2606 OID 24751)
-- Name: recipient recipient_pkey; Type: CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY public.recipient
    ADD CONSTRAINT recipient_pkey PRIMARY KEY (uid);


--
-- TOC entry 2071 (class 2606 OID 24753)
-- Name: setting setting_pkey; Type: CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY public.setting
    ADD CONSTRAINT setting_pkey PRIMARY KEY (uid);


--
-- TOC entry 2078 (class 2606 OID 32830)
-- Name: temp temp_pkey; Type: CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY public.temp
    ADD CONSTRAINT temp_pkey PRIMARY KEY (uid);


--
-- TOC entry 2075 (class 2606 OID 24755)
-- Name: user user_pkey; Type: CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (uid);


--
-- TOC entry 2060 (class 1259 OID 24756)
-- Name: idx_unique_controller_name; Type: INDEX; Schema: public; 
--

CREATE UNIQUE INDEX idx_unique_controller_name ON public.controller USING btree (lower((name)::text));


--
-- TOC entry 2063 (class 1259 OID 24757)
-- Name: idx_unique_recipient_name; Type: INDEX; Schema: public; 
--

CREATE UNIQUE INDEX idx_unique_recipient_name ON public.recipient USING btree (lower((name)::text));


--
-- TOC entry 2068 (class 1259 OID 24758)
-- Name: idx_unique_sensor_name; Type: INDEX; Schema: public; 
--

CREATE UNIQUE INDEX idx_unique_sensor_name ON public.sensor USING btree (lower((name)::text));


--
-- TOC entry 2069 (class 1259 OID 24759)
-- Name: idx_unique_setting_name; Type: INDEX; Schema: public; 
--

CREATE UNIQUE INDEX idx_unique_setting_name ON public.setting USING btree (lower((name)::text));


--
-- TOC entry 2076 (class 1259 OID 32831)
-- Name: idx_unique_temp; Type: INDEX; Schema: public; 
--

CREATE UNIQUE INDEX idx_unique_temp ON public.temp USING btree (sensor_uid);


--
-- TOC entry 2072 (class 1259 OID 24760)
-- Name: idx_unique_user_login; Type: INDEX; Schema: public; 
--

CREATE UNIQUE INDEX idx_unique_user_login ON public."user" USING btree (lower((login)::text));


--
-- TOC entry 2073 (class 1259 OID 24761)
-- Name: idx_unique_user_name; Type: INDEX; Schema: public; 
--

CREATE UNIQUE INDEX idx_unique_user_name ON public."user" USING btree (lower((name)::text));


--
-- TOC entry 2082 (class 2606 OID 24762)
-- Name: subscription fk_reg_recipient_controller2controller; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY public.subscription
    ADD CONSTRAINT fk_reg_recipient_controller2controller FOREIGN KEY (controller_uid) REFERENCES public.controller(uid);


--
-- TOC entry 2083 (class 2606 OID 24767)
-- Name: subscription fk_reg_recipient_controller2recipient; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY public.subscription
    ADD CONSTRAINT fk_reg_recipient_controller2recipient FOREIGN KEY (recipient_uid) REFERENCES public.recipient(uid);


--
-- TOC entry 2081 (class 2606 OID 24772)
-- Name: sensor fk_sensor2controller; Type: FK CONSTRAINT; Schema: public; 
--

ALTER TABLE ONLY public.sensor
    ADD CONSTRAINT fk_sensor2controller FOREIGN KEY (controller_uid) REFERENCES public.controller(uid);


-- Completed on 2019-06-02 20:57:30

--
-- PostgreSQL database dump complete
--

--начальные данные
INSERT INTO public.controller (uid, name, created_datetime, url) VALUES(
'a28c07b1-a0a1-4de8-897c-0550db98b666',	'Мега',	'2019-05-07 01:50:50.182',	'http://anyoffice42.asuscomm.com:41337/tiny.htm'
);
---
INSERT INTO public.recipient (uid, name, created_datetime, fcm_token) VALUES(
'33c3ccc7-505b-4125-baab-c4de93a7b74f',	'Стас999',	'2019-05-12 16:06:03.999',	'dTCzAHOae-Q:APA91bFrvlWe8upbXM1SkEs-_wISiZL706r9My6C1OjPk3ILySPmLfPSGrVWLNp6b_vcxH5BH_05esouYDi3ZcWOcBl4vIL8hiDJnMm1t0h0aQ8Xp1ckFPlDJi1UA89H8wiVcVnpcNTi'
);
---
INSERT INTO public.sensor (uid, name, controller_uid, created_datetime, minvalue, maxvalue, num) VALUES(
'56351b12-b35b-4798-9924-d4618d96c99a',	'Холодильник 2',	'a28c07b1-a0a1-4de8-897c-0550db98b666',	'2019-05-07 01:56:19.626',	0.0,	0.0,	2);
INSERT INTO public.sensor (uid, name, controller_uid, created_datetime, minvalue, maxvalue, num) VALUES(
'4ce1645e-8516-489f-b58e-d6a10ca0dff8',	'Холодильник 3',	'a28c07b1-a0a1-4de8-897c-0550db98b666',	'2019-05-07 02:01:01.200',	0.0,	4.0, 3);
INSERT INTO public.sensor (uid, name, controller_uid, created_datetime, minvalue, maxvalue, num) VALUES(
'784dabb8-dc69-49ba-9a9e-22cdd01af6e9',	'Холодильник 4',	'a28c07b1-a0a1-4de8-897c-0550db98b666',	'2019-05-07 02:02:46.128',	0.0,	0.0,	4);
INSERT INTO public.sensor (uid, name, controller_uid, created_datetime, minvalue, maxvalue, num) VALUES(
'174b1805-4421-4335-a1d6-42a1806064e6',	'Холодильник 5',	'a28c07b1-a0a1-4de8-897c-0550db98b666',	'2019-05-07 02:04:58.328',	0.0,	0.0,	5);
INSERT INTO public.sensor (uid, name, controller_uid, created_datetime, minvalue, maxvalue, num) VALUES(
'8ccda69f-3423-44b6-93a2-bd531406d1f9',	'Холодильник 6',	'a28c07b1-a0a1-4de8-897c-0550db98b666',	'2019-05-07 02:16:13.942',	0.0,	0.0,	6);
INSERT INTO public.sensor (uid, name, controller_uid, created_datetime, minvalue, maxvalue, num) VALUES(
'ae4e840b-dbcb-4aaf-9fc4-b00b321c2e40',	'Холодильник 7',	'a28c07b1-a0a1-4de8-897c-0550db98b666',	'2019-05-07 02:16:59.322',	0.0,	0.0,	7);
INSERT INTO public.sensor (uid, name, controller_uid, created_datetime, minvalue, maxvalue, num) VALUES(
'5eed7edd-9361-41b6-811d-8cd558d2375f',	'Холодильник 8',	'a28c07b1-a0a1-4de8-897c-0550db98b666',	'2019-05-07 02:18:39.128',	0.0,	0.0,	8);
INSERT INTO public.sensor (uid, name, controller_uid, created_datetime, minvalue, maxvalue, num) VALUES(
'44ecb10e-8c98-4e52-b91d-a4de856815e3',	'Холодильник 0',	'a28c07b1-a0a1-4de8-897c-0550db98b666',	'2019-05-07 01:51:40.802',	19.1,	20.2,	0);
INSERT INTO public.sensor (uid, name, controller_uid, created_datetime, minvalue, maxvalue, num) VALUES(
'ca90c5ff-e303-4cda-bf7e-05274f6077be',	'Холодильник 1',	'a28c07b1-a0a1-4de8-897c-0550db98b666',	'2019-05-07 01:54:47.979',	12.5,	45.4,	1);
---
INSERT INTO public.setting (uid, name, value, created_datetime) VALUES(
'36a6fafa-6931-4bff-bbbe-aad5cd0cf06b',	'eventHubURL',	'https://fcm.googleapis.com/fcm/send',	'2019-05-19 01:40:08.27');
INSERT INTO public.setting (uid, name, value, created_datetime) VALUES(
'cadf799e-f853-4fc1-8854-33a3af97a2c8',	'eventHubAuthorizationKey',	'key=AAAAWa_hJDY:APA91bFpC7NkOPtJwAOX4dq-W3bjYJSXzcixHolAncqMp9IcNAkJ7QXdOnKhVXF3icmoGGUeGDs7FbOW2_uXFjXBd2m1M7xg4zeEPVCO-0WZkW6VHqvIHVhIAw2CWqwHGbf7HVovSMVu',	'2019-05-19 01:29:26.897');
INSERT INTO public.setting (uid, name, value, created_datetime) VALUES(
'8f584ace-fb09-4817-88be-b570c79660c2',	'dbVersion',	'1.0.0',	'2019-06-02 17:49:12.157');
---
INSERT INTO public.subscription (recipient_uid, controller_uid, uid, created_datetime, notify_over, notify_error) VALUES(
'33c3ccc7-505b-4125-baab-c4de93a7b74f',	'a28c07b1-a0a1-4de8-897c-0550db98b666',	'2111284e-30c7-4cf0-af3c-c70d2fc38af2',	'2019-05-12 03:23:55.519',	true,	true
);
---
INSERT INTO public."user" (uid, name, login, password, role, created_datetime) VALUES(
'c75c8909-0ac2-e1c2-775b-8ea4ee2985cc',	'stas',	'stas',	'$2a$10$VdDKBq8ZjezYN.tXYADrqui/wil9JEK5ZPQ3EmEnTAocpmX7TN8Km',	'recipient',	'2019-05-10 00:40:25.070671');
INSERT INTO public."user" (uid, name, login, password, role, created_datetime) VALUES(
'ede17929-a986-b4b9-5577-ceff5c5b82ea',	'admin',	'admin',	'$2a$10$pqvxD0ncirSx.KuEpx9XQ.X0cJaOkJS5Vx.Z6l8U4o42iPeGTiHg.',	'admin',	'2019-05-10 00:41:19.073392');
INSERT INTO public."user" (uid, name, login, password, role, created_datetime) VALUES(
'b9178b62-ecc7-f54d-bdc9-899cac45cbad',	'restapi',	'restapi',	'$2a$10$hZS4S/XlFeT9.5afk6NQcecCCYi0BrOJuBteRa/QJ.w3atuJX7Xay',	'restapi',	'2019-05-10 18:15:57.98017');
---
update public.setting set value = '1.0.0' where name = 'dbVersion';