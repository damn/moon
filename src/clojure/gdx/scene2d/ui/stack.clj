(ns clojure.gdx.scene2d.ui.stack
  (:require [clojure.gdx.scene2d.actor :as actor]
            [clojure.gdx.scene2d.group :as group])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Stack)))

(defmethod actor/create :ui/stack
  [opts]
  (doto (Stack.)
    (group/set-opts! opts)))
