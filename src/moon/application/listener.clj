(ns moon.application.listener
  (:require [clojure.application-listener :refer [application-listener]]))

(defn f
  [{:keys [state-var
           create-pipeline
           dispose!
           render-pipeline
           resize!]}]
  (application-listener
   (let [state @state-var]
     {:create! (fn []
                 (reset! state (reduce (fn [ctx [f & params]]
                                         (apply f ctx params))
                                       {}
                                       create-pipeline)))

      :dispose! (fn []
                  (dispose! @state))

      :render! (fn []
                 (swap! state (fn [ctx]
                                (reduce (fn [ctx [f & params]]
                                          (apply f ctx params))
                                        ctx
                                        render-pipeline))))

      :resize! (fn [width height]
                 (resize! @state width height))

      :pause! (fn [])

      :resume! (fn [])})))
