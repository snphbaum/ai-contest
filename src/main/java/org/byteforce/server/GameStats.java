package org.byteforce.server;

import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * @author Philipp Baumgaertel
 */
// @Entity
// @Table(name = "kca", schema = "test", catalog = "")
public class GameStats
{
    // private int idkca;
    //
    // private String kcacol;
    //
    //
    // //private Set<KcaPartEntity> kcaPartEntitySet;
    //
    // public GameStats(){
    //
    //
    // }
    //
    // public GameStats(int pIdkca, String pKcacol){
    //     idkca = pIdkca;
    //     kcacol = pKcacol;
    // }
    //
    // @Id
    // @Column(name = "idkca")
    // public int getIdkca()
    // {
    //     return idkca;
    // }
    //
    //
    //
    // public void setIdkca(final int pIdkca)
    // {
    //     idkca = pIdkca;
    // }
    //
    //
    //
    // @Basic
    // @Column(name = "kcacol")
    // public String getKcacol()
    // {
    //     return kcacol;
    // }
    //
    //
    //
    // public void setKcacol(final String pKcacol)
    // {
    //     kcacol = pKcacol;
    // }
    //
    // @OneToMany(mappedBy="kcaEntity") //, fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    // //@Fetch(FetchMode.SUBSELECT)
    // public Set<KcaPartEntity> getKcaPartEntitySet()
    // {
    //     return kcaPartEntitySet;
    // }
    //
    //
    //
    // public void setKcaPartEntitySet(final Set<KcaPartEntity> pKcaPartEntitySet)
    // {
    //     kcaPartEntitySet = pKcaPartEntitySet;
    // }
    //
    //
    //
    // @Override
    // public boolean equals(final Object pO)
    // {
    //     if (this == pO) {
    //         return true;
    //     }
    //     if (pO == null || getClass() != pO.getClass()) {
    //         return false;
    //     }
    //
    //     GameStats kcaEntity = (GameStats) pO;
    //
    //     if (idkca != kcaEntity.idkca) {
    //         return false;
    //     }
    //     if (kcacol != null ? !kcacol.equals(kcaEntity.kcacol) : kcaEntity.kcacol != null) {
    //         return false;
    //     }
    //
    //     return true;
    // }
    //
    //
    //
    // @Override
    // public int hashCode()
    // {
    //     int result = idkca;
    //     result = 31 * result + (kcacol != null ? kcacol.hashCode() : 0);
    //     return result;
    // }
}
