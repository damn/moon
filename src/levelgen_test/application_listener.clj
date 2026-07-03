(ns levelgen-test.application-listener
  (:require levelgen-test.create
            levelgen-test.dispose
            levelgen-test.render
            levelgen-test.resize))

(defn f [state config]
  {:create! (fn []
              (reset! state (levelgen-test.create/f config)))

   :dispose! (fn []
               (levelgen-test.dispose/f @state))

   :render! (fn []
              (swap! state levelgen-test.render/f))

   :resize! (fn [width height]
              (levelgen-test.resize/f @state width height))

   :pause! (fn [])

   :resume! (fn [])})
