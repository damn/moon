(ns gdx.lwjgl
  (:require [com.badlogic.gdx.application-listener :refer [application-listener]]
            [com.badlogic.gdx.backends.lwjgl.application :as application]
            [com.badlogic.gdx.backends.lwjgl.application-config :as config]
            [com.badlogic.gdx.gdx :as gdx]))

(defn application!
  [{:keys [state-var
           create-pipeline
           dispose!
           render-pipeline
           resize!]
    :as config}]
  (application/create (application-listener
                       (let [state @state-var]
                         {:create! (fn []
                                     (reset! state (reduce (fn [ctx [f & params]]
                                                             (apply f ctx params))
                                                           {
                                                            :ctx/audio    (gdx/audio)
                                                            :ctx/files    (gdx/files)
                                                            :ctx/graphics (gdx/graphics)
                                                            :ctx/input    (gdx/input)
                                                            }
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
