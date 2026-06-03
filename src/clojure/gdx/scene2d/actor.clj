(ns clojure.gdx.scene2d.actor
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn set-user-object! [^Actor actor user-object]
  (.setUserObject actor user-object))

(defn add-listener! [^Actor actor listener]
  (.addListener actor listener))

(defn set-visible! [^Actor actor visible?]
  (.setVisible actor visible?))

(defn set-touchable! [^Actor actor touchable]
  (.setTouchable actor touchable))

(defn set-name! [^Actor actor name]
  (.setName actor name))

(defn remove! [^Actor actor]
  (.remove actor))

(defn get-stage [^Actor actor]
  (.getStage actor))
