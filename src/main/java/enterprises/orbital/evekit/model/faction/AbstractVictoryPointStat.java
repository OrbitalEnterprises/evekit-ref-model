package enterprises.orbital.evekit.model.faction;

import enterprises.orbital.evekit.model.RefCachedData;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractVictoryPointStat extends RefCachedData {
  @Enumerated(EnumType.STRING)
  protected StatAttribute attribute;
  protected int victoryPoints;

  AbstractVictoryPointStat(StatAttribute attribute, int victoryPoints) {
    super();
    this.attribute = attribute;
    this.victoryPoints = victoryPoints;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equivalent(
      RefCachedData sup) {
    if (!(sup instanceof AbstractVictoryPointStat)) return false;
    AbstractVictoryPointStat other = (AbstractVictoryPointStat) sup;
    return attribute == other.attribute && victoryPoints == other.victoryPoints;
  }

  public StatAttribute getAttribute() {
    return attribute;
  }

  public int getVictoryPoints() {
    return victoryPoints;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
    result = prime * result + victoryPoints;
    return result;
  }

  @Override
  public boolean equals(
      Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    AbstractVictoryPointStat other = (AbstractVictoryPointStat) obj;
    return attribute == other.attribute && victoryPoints == other.victoryPoints;
  }

  @Override
  public String toString() {
    return "AbstractVictoryPointStat [attribute=" + attribute + ", victoryPoints=" + victoryPoints + "]";
  }

}
