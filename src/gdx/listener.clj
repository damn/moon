(ns gdx.listener
  (:require [com.badlogic.gdx.application-listener :refer [application-listener]]))

; TODO
; 1. move application-listener one up
; 2. initial context? @ state-var ?
; 3. dispose/resize pipelined

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
