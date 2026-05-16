(ns clojure.gdx.application-listener
  (:require [com.badlogic.gdx.application-listener :as listener]
            [com.badlogic.gdx.gdx :as gdx]))

(defn create
  [{:keys [state-var
           create
           dispose
           render
           resize]}]
  (let [state @state-var]
    (listener/create
     {:create! (fn []
                 (reset! state
                         (reduce (fn [ctx [f & params]]
                                   (apply f ctx params))
                                 {:ctx/app (gdx/app)}
                                 create)))
      :dispose! (fn []
                  (doseq [f dispose]
                    (f @state)))
      :render! (fn []
                 (swap! state
                        (fn [ctx]
                          (reduce (fn [ctx [f & params]]
                                    (apply f ctx params))
                                  ctx
                                  render))))
      :resize! (fn [width height]
                 (doseq [f resize]
                   (f @state width height)))
      :pause! (fn [])
      :resume! (fn [])})))
