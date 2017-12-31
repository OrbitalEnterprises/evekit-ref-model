package enterprises.orbital.evekit.model.faction;

import enterprises.orbital.evekit.model.RefCachedData;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractKillStat extends RefCachedData {
  @Enumerated(EnumType.STRING)
  protected StatAttribute attribute;
  int kills;

  AbstractKillStat(StatAttribute attribute, int kills) {
    super();
    this.attribute = attribute;
    this.kills = kills;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      RefCachedData sup) {
    if (!(sup instanceof AbstractKillStat)) return false;
    AbstractKillStat other = (AbstractKillStat) sup;
    return attribute == other.attribute && kills == other.kills;
  }

  public StatAttribute getAttribute() {
    return attribute;
  }

  public int getKills() {
    return kills;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
    result = prime * result + kills;
    return result;
  }

  @Override
  public boolean equals(
      Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    AbstractKillStat other = (AbstractKillStat) obj;
    return attribute == other.attribute && kills == other.kills;
  }

  @Override
  public String toString() {
    return "AbstractKillStat [attribute=" + attribute + ", kills=" + kills + "]";
  }

}
