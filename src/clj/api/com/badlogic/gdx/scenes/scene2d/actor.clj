(ns clj.api.com.badlogic.gdx.scenes.scene2d.actor
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn add-listener! [^Actor actor listener]
  (.addListener actor listener))

(defn user-object [^Actor actor]
  (.getUserObject actor))

(defn stage [^Actor actor]
  (.getStage actor))

(defn set-position! [^Actor actor [x y]]
  (.setPosition actor x y))

(defn set-visible! [^Actor actor visible?]
  (.setVisible actor visible?))

(defn visible? [^Actor actor]
  (.isVisible actor))

(defn parent [^Actor actor]
  (.getParent actor))

(defn remove! [^Actor actor]
  (.remove actor))

(defn set-name! [^Actor actor name]
  (.setName actor name))
