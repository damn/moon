(ns com.badlogic.gdx.scenes.scene2d.ui.horizontal-group
  (:refer-clojure :exclude [new])
  (:import
           (com.badlogic.gdx.scenes.scene2d.ui HorizontalGroup)
           ))

(defn new []
  (HorizontalGroup.))

(defn pad! [^HorizontalGroup group n]
  (HorizontalGroup/.pad group (float n)))

(defn space! [^HorizontalGroup group n]
  (HorizontalGroup/.space group (float n)))
