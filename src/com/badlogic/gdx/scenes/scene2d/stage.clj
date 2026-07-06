(ns com.badlogic.gdx.scenes.scene2d.stage
  (:import (com.badlogic.gdx.scenes.scene2d Stage)
           (moon Stage)))

(defn act [stage]
  (Stage/.act stage))

(defn add-actor! [stage actor]
  (Stage/.addActor stage actor))

(defn draw [stage]
  (Stage/.draw stage))

(defn hit [^Stage stage x y touchable?]
  (.hit stage (float x) (float y) touchable?))

(defn set-ctx! [stage ctx]
  (set! (.ctx ^Stage stage) ctx))
