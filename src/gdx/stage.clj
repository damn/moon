(ns gdx.stage
  (:import (clojure.lang ILookup)
           (clojure Stage)
           (com.badlogic.gdx.scenes.scene2d Actor)))

(defn set-ctx! [^Stage stage ctx]
  (set! (.ctx stage) ctx))

(defn create [viewport batch]
  (proxy [Stage ILookup] [viewport batch]
    (valAt [k]
      (case k
        :stage/root     (.getRoot     ^Stage this)
        :stage/ctx      (.ctx         ^Stage this)
        :stage/viewport (.getViewport ^Stage this)))))

(defn apply-ctx! [stage f]
  (set-ctx! stage (f (:stage/ctx stage))))

(defn add-actor! [stage actor]
  (.addActor ^Stage stage ^Actor actor))

(defn hit [stage x y touchable?]
  (.hit ^Stage stage (float x) (float y) touchable?))

(defn act! [stage]
  (.act ^Stage stage))

(defn draw! [stage]
  (.draw ^Stage stage))
