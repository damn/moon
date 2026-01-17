(ns moon.create.add-leftout-keys)

(defn step [ctx]
  (assoc ctx
         :ctx/mouseover-eid nil
         :ctx/paused? false
         :ctx/world-mouse-position nil
         :ctx/ui-mouse-position nil
         :ctx/delta-time nil
         :ctx/elapsed-time 0
         :ctx/active-entities nil
         ))
