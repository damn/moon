(ns clojure.levelgen-test-application-listener
  (:require clojure.levelgen-test-create
            clojure.levelgen-test-dispose
            clojure.levelgen-test-render
            clojure.levelgen-test-resize))

(defn f [state config]
  {:create! (fn []
              (reset! state (clojure.levelgen-test-create/f config)))

   :dispose! (fn []
               (clojure.levelgen-test-dispose/f @state))

   :render! (fn []
              (swap! state clojure.levelgen-test-render/f))

   :resize! (fn [width height]
              (clojure.levelgen-test-resize/f @state width height))

   :pause! (fn [])

   :resume! (fn [])})
