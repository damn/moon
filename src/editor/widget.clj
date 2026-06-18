(ns editor.widget
  (:require [com.badlogic.gdx.scenes.scene2d.actor.set-user-object :refer [set-user-object!]]))

(defmulti create (fn [[schema-k :as _schema] v ctx]
                   schema-k))

(defn build [ctx schema k v]
  (let [widget (create schema v ctx)] ; - wait its used also somewhere else w/o this widget/create?
    ; FIXME assert no user object !
    (set-user-object! widget [k v])
    widget))

(defmulti value (fn [[schema-k :as _schema] widget schemas]
                  schema-k))
