(ns clojure.gdx.scene2d.ui.stack
  (:require [com.badlogic.gdx.scene2d.ui.stack :as stack]
            [clojure.gdx.scene2d.actor :as actor]
            [clojure.gdx.scene2d.group :as group]))

(defmethod actor/create :ui/stack
  [opts]
  (doto (stack/create)
    (group/set-opts! opts)))
