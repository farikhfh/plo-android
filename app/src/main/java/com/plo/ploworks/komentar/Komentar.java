package com.plo.ploworks.komentar;

import org.json.JSONArray;

/**
 * Created by Farikh Fadlul Huda on 11/4/2015.
 */
public class Komentar {
    Integer no;
    String username, url_pp, nama, isi, waktu, url_gambar;
    JSONArray tanggapan;

    Boolean hasTanggapan;

    public Boolean getHasTanggapan() {
        return hasTanggapan;
    }

    public void setHasTanggapan(Boolean hasTanggapan) {
        this.hasTanggapan = hasTanggapan;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUrl_pp() {
        return url_pp;
    }

    public void setUrl_pp(String url_pp) {
        this.url_pp = url_pp;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getIsi() {
        return isi;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getUrl_gambar() {
        return url_gambar;
    }

    public void setUrl_gambar(String url_gambar) {
        this.url_gambar = url_gambar;
    }

    public JSONArray getTanggapan() {
        return tanggapan;
    }

    public void setTanggapan(JSONArray tanggapan) {
        this.tanggapan = tanggapan;
    }
}
