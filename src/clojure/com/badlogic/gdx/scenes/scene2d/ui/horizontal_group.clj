(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.horizontal-group
  (:import (com.badlogic.gdx.scenes.scene2d.ui HorizontalGroup)))

(defn create []
  (HorizontalGroup.))

(defn space! [^HorizontalGroup horizontal-group space]
  (.space horizontal-group (float space)))

(defn pad! [^HorizontalGroup horizontal-group pad]
  (.pad horizontal-group pad))
