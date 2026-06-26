(ns com.badlogic.gdx.scenes.scene2d.ui.horizontal-group
  (:import (com.badlogic.gdx.scenes.scene2d.ui HorizontalGroup)))

(defn create []
  (HorizontalGroup.))

(defn set-space! [^HorizontalGroup group space]
  (.space group space))

(defn set-pad! [^HorizontalGroup group pad]
  (.pad group pad))
