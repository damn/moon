(ns com.badlogic.gdx.scenes.scene2d.stage
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn act [stage]
  (.act ^Stage stage))

(defn addActor [stage actor]
  (.addActor ^Stage stage actor))

(defn draw [stage]
  (.draw ^Stage stage))

(defn hit [^Stage stage x y touchable?]
  (.hit stage (float x) (float y) touchable?))
