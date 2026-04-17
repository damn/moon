(ns clojure.gdx.scene2d.ui.stack
  (:require [clojure.scene2d.group :as group])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Stack)))

(defn create [opts]
  (doto (Stack.)
    (group/set-opts! opts)))
