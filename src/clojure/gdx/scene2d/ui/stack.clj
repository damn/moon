(ns clojure.gdx.scene2d.ui.stack
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.stack :as stack]
            [clojure.scene2d.group :as group]))

(defn create [opts]
  (doto (stack/create)
    (group/set-opts! opts)))
