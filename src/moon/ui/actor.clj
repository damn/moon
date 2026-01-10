(ns moon.ui.actor
  (:refer-clojure :exclude [name])
  (:require [moon.ui.stage :as stage])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(def set-name!                Actor/.setName)
(def set-user-object!         Actor/.setUserObject)
(def set-visible!             Actor/.setVisible)
(def set-touchable!           Actor/.setTouchable)
(def set-position!            Actor/.setPosition)
(def add-listener!            Actor/.addListener)
(def remove!                  Actor/.remove)
(def stage                    Actor/.getStage)
(def user-object              Actor/.getUserObject)
(def width                    Actor/.getWidth)
(def height                   Actor/.getHeight)
(def x                        Actor/.getX)
(def y                        Actor/.getY)
(def parent                   Actor/.getParent)
(def name                     Actor/.getName)
(def visible?                 Actor/.isVisible)
(def stage->local-coordinates Actor/.stageToLocalCoordinates)
(def hit                      Actor/.hit)

(def opts-fn-map
  {:actor/name        set-name!
   :actor/user-object set-user-object!
   :actor/visible?    set-visible!
   :actor/touchable   set-touchable!
   :actor/listener    add-listener!
   :actor/position (fn [a [x y]]
                     (set-position! a x y))
   :actor/center-position (fn [a [x y]]
                            (set-position! a
                                           (- x (/ (width  a) 2))
                                           (- y (/ (height a) 2))))})

(defn set-opts! [actor opts]
  (doseq [[k v] opts
          :let [f (get opts-fn-map k)]
          :when f]
    (f actor v))
  actor)

(defn create
  [{:keys [act draw] :as opts}]
  (-> (proxy [Actor] []
        (act [delta]
          (act this delta)
          (let [^Actor this this]
            (proxy-super act delta)))
        (draw [batch parent-alpha]
          (draw this batch parent-alpha)))
      (set-opts! opts)))

(defmethod stage/build :actor/actor
  [opts]
  (create opts))
