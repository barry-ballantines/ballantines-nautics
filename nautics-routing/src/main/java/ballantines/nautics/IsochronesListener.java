/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ballantines.nautics;

import java.util.Date;
import java.util.List;

/**
 *
 * @author mbuse
 */
public interface IsochronesListener {
  
   void isochronesCalculated(Date date, List<Leg> isochrones);
   
   void winningLegFound(Leg winningLeg);
   
   void noLegFound();
}
