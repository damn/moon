(ns moon.application.create.unorganised)

(defn step [ctx]
  (assoc ctx

         ;frame
          :ctx/active-entities nil
          :ctx/delta-time nil
          :ctx/ui-mouse-position nil
          :ctx/world-mouse-position nil
          :ctx/mouseover-eid nil
          :ctx/paused? false

          ; stuff
          :ctx/unit-scale (atom 1)
          :ctx/elapsed-time 0
          :ctx/potential-field-cache (atom nil)
          :ctx/id-counter (atom 0)
          :ctx/entity-ids (atom {})

          ; constants (config & not state?)
          :ctx/factions-iterations {:good 15 :evil 5}
          :ctx/max-delta 0.04
          :ctx/minimum-size 0.39
          :ctx/z-orders [:z-order/on-ground
                         :z-order/ground
                         :z-order/flying
                         :z-order/effect]
          ))
