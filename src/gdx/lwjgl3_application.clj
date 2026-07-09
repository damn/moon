(ns gdx.lwjgl3-application
  (:require [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as app]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration :as config]))

(let [k->opts
      {
       :config/set-title          config/set-title
       :config/set-windowed-mode  config/set-windowed-mode
       :config/set-foreground-fps config/set-foreground-fps
       }
      ]
  (defn create [application-listener
                config-opts]
    (app/new application-listener
             (let [config (config/new)]
               (doseq [[k v] config-opts]
                 (let [apply! (k->opts k)]
                   (assert apply! (str "Unknown lwjgl3 config option: " k))
                   (apply! config v)))
               config))))
