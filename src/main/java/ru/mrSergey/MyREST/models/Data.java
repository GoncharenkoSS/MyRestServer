package ru.mrSergey.MyREST.models;

import jakarta.persistence.*;

@Entity
@Table(name = "data")
public class Data {
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        @Column(name = "value")
        private String value;

        @Column(name = "raining")
        private String raining;

        @ManyToOne
        @Column(name = "id_sensor")
        private Sensor owner;

        public Data(String value, String raining) {
                this.value = value;
                this.raining = raining;
        }
        public Data(){}

        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
        }

        public String getValue() {
                return value;
        }

        public void setValue(String value) {
                this.value = value;
        }

        public String getRaining() {
                return raining;
        }

        public void setRaining(String raining) {
                this.raining = raining;
        }

        public Sensor getOwner() {
                return owner;
        }

        public void setOwner(Sensor owner) {
                this.owner = owner;
        }
}
