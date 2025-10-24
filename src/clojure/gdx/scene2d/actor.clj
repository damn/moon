(ns clojure.gdx.scene2d.actor
  (:refer-clojure :exclude [name])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn create [{:keys [act draw]}]
  (proxy [Actor] []
    (act [delta]
      (act this delta)
      (let [^Actor this this]
        (proxy-super act delta)))
    (draw [batch parent-alpha]
      (draw this batch parent-alpha))))

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
