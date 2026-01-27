; finally the whole thing is 'clj.api.*'?
(ns moon.ui.actor
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(def ^:private opts-fn-map
  {
   ; 10 usage
   :actor/name        Actor/.setName

   ; 2 usage
   :actor/user-object Actor/.setUserObject

   })

(defn set-position! [^Actor actor [x y]]
  (.setPosition actor x y))

; setPosition has alignment param -> maybe can use?
; but anyway prefer to write clojure instead of java
(defn set-center! [^Actor actor [x y]]
  (.setPosition actor
                (- x (/ (.getWidth  actor) 2))
                (- y (/ (.getHeight actor) 2))))

; finnally just expose the functions and do 'doto ' on a more top lvl ?
(defn set-opts! [actor opts]
  (doseq [[k v] opts
          :let [f (get opts-fn-map k)]
          :when f]
    (f actor v))
  actor)

; these two are simple 'clj.api.com.badlogic.gdx.scenes.scene2d.actor' fns ?!

(defn toggle-visible! [^Actor actor] ; maybe make PR @ libgdx?
  (.setVisible actor (not (.isVisible actor))))

(defn find-ancestor ; maybe function exists already? firstAscendant ?
  [^Actor actor clazz]
  (if-let [parent (.getParent actor)]
    (if (instance? clazz parent)
      parent
      (find-ancestor parent clazz))
    (throw (Error. (str "Actor has no parent window " actor)))))
