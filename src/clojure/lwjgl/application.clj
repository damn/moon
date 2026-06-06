(ns clojure.lwjgl.application
  (:require [clojure.gdx :as gdx]
            [clojure.gdx.application-listener :as application-listener]
            [gdx.backends.lwjgl.application :as application]
            [gdx.backends.lwjgl.application-config :as config]))

(defn start!
  [{:keys [state-var
           create-pipeline
           dispose!
           render-pipeline
           resize!]
    :as config}]
  (config/use-glfw-async!)
  (application/create (application-listener/create
                       (let [state @state-var]
                         {:create! (fn []
                                     (reset! state (reduce (fn [ctx [f & params]]
                                                             (apply f ctx params))
                                                           {:ctx/app (gdx/app)}
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

                          :resume! (fn [])}))
                      (config/create config)))
