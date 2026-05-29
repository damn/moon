(ns create.unorganised
  (:require [gdx.scenes.scene2d.ui.tooltip-manager :as tooltip-manager]
            [gdx.graphics.colors :as colors]))

(defn step [ctx]
  (tooltip-manager/set-initial-time! 0)
  (colors/put! {"PRETTY_NAME" [0.84 0.8 0.52 1]})
  (assoc ctx
         :ctx/world-unit-scale (float (/ 48))
         :ctx/unit-scale (atom 1)

         ;frame
         :ctx/active-entities nil
         :ctx/delta-time nil
         :ctx/ui-mouse-position nil
         :ctx/world-mouse-position nil
         :ctx/mouseover-eid nil
         :ctx/paused? false

         ; stuff
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
