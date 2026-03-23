(ns moon.create.add-leftout-keys) ; TODO to listener/create

(defn step [ctx]
  (assoc ctx

         :ctx/active-entities nil ; ???
         ; ?? what is this
         ; => I grep it ( in a certain way)
         ; => I vimgrep
         ; =? get drawn, filtered for active target,
         ; and ticked
         ; => '' DOCUMENTATION ''
         ; => savein the repo and grep also so will see changes
         ; store information about the keyword ' SOMEWHERE ' ?


         :ctx/delta-time nil
         :ctx/mouseover-eid nil
         :ctx/ui-mouse-position nil
         :ctx/world-mouse-position nil

         :ctx/elapsed-time 0
         :ctx/paused? false

         ))
