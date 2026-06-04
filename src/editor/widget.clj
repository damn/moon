(ns editor.widget
  (:require [clojure.string.truncate :refer [truncate]]
            [clojure.core.edn-str :refer [->edn-str]]
            [clojure.gdx.scene2d.actor.set-user-object :refer [set-user-object!]]
            [clojure.gdx.scene2d.actor.get-user-object :refer [get-user-object]]
            [clojure.gdx.scene2d.ui.label :as label]))

(defmulti create (fn [[schema-k :as _schema] v ctx]
                   schema-k))

(defn build [ctx schema k v]
  (let [widget (create schema v ctx)] ; - wait its used also somewhere else w/o this widget/create?
    ; FIXME assert no user object !
    (set-user-object! widget [k v])
    widget))

(defmulti value (fn [[schema-k :as _schema] widget schemas]
                  schema-k))

(defmethod create :default
  [_ v {:keys [ctx/skin]}]
  (label/create
   {:text (truncate (->edn-str v) 60)
    :skin skin}))

(defmethod value :default
  [_  widget _schemas]
  ((get-user-object widget) 1))
