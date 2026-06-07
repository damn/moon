(ns com.badlogic.gdx.backends.lwjgl
  (:require [com.badlogic.gdx :as gdx]
            [com.badlogic.gdx.application-listener :refer [application-listener]]
            [com.badlogic.gdx.backends.lwjgl.application :as application]
            [com.badlogic.gdx.backends.lwjgl.application-config :as config]
            [com.badlogic.gdx.utils.shared-library-loader :as shared-library-loader]
            [com.badlogic.gdx.utils.os :as os]
            [lwjgl.system.configuration]))

(defn application!
  [{:keys [state-var
           create-pipeline
           dispose!
           render-pipeline
           resize!]
    :as config}]
  (when (= (shared-library-loader/os) os/mac-os)
    (lwjgl.system.configuration/set-glfw-library-name! "glfw_async"))
  (application/create (application-listener
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
