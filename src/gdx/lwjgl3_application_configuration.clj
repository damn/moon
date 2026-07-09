(ns gdx.lwjgl3-application-configuration
  (:require [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration :as this]))

(let [k->opts
      {
       :title          this/set-title!
       :windowed-mode  this/set-windowed-mode!
       :foreground-fps this/set-foreground-fps!
       }
      ]
  (defn build [opts]
    (let [configuration (this/create)]
      (doseq [[k v] opts]
        (let [apply! (k->opts k)]
          (assert apply! (str "Unknown lwjgl3 config option: " k))
          (apply! configuration v)))
      configuration)))
