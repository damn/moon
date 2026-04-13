(ns moon.ui.stack
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.stack :as stack]
            [moon.ui.group :as group]))

(defn create [opts]
  (doto (stack/create)
    (group/set-opts! opts)))
