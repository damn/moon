(ns gdl.scenes.scene2d.actor
  (:refer-clojure :exclude [new remove])
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]))

(defn add-listener [& args]
  (apply actor/addListener args))

(defn get-height [& args]
  (apply actor/getHeight args))

(defn get-name [& args]
  (apply actor/getName args))

(defn get-parent [& args]
  (apply actor/getParent args))

(defn get-stage [& args]
  (apply actor/getStage args))

(defn get-user-object [& args]
  (apply actor/getUserObject args))

(defn get-width [& args]
  (apply actor/getWidth args))

(defn get-x [& args]
  (apply actor/getX args))

(defn get-y [& args]
  (apply actor/getY args))

(defn hit [& args]
  (apply actor/hit args))

(defn remove-actor [& args]
  (apply actor/remove args))

(defn set-name [& args]
  (apply actor/setName args))

(defn set-position
  ([actor x y]
   (actor/setPosition actor x y))
  ([actor x y align]
   (actor/setPosition actor x y align)))

(defn set-touchable [& args]
  (apply actor/setTouchable args))

(defn set-user-object [& args]
  (apply actor/setUserObject args))

(defn set-visible [& args]
  (apply actor/setVisible args))

(defn stage-to-local-coordinates [& args]
  (apply actor/stageToLocalCoordinates args))

(defn visible? [& args]
  (apply actor/isVisible args))

(defn new [& args]
  (apply actor/new args))
