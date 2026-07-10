(ns com.badlogic.gdx.scenes.scene2d.ui.horizontal-group
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.scenes.scene2d.ui HorizontalGroup)))

(defn new []
  (HorizontalGroup.))

(defn pad [^HorizontalGroup group n]
  (.pad group (float n)))

(defn space [^HorizontalGroup group n]
  (.space group (float n)))
