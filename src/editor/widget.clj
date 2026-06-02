(ns editor.widget
  (:require [clojure.string.truncate :refer [truncate]]
            [clojure.core.edn-str :refer [->edn-str]]
            [gdx.scenes.scene2d.ui.label :as label])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defmulti create (fn [[schema-k :as _schema] v ctx]
                   schema-k))

(defn build [ctx schema k v]
  (let [widget (create schema v ctx)] ; - wait its used also somewhere else w/o this widget/create?
    ; FIXME assert no user object !
    (Actor/.setUserObject widget [k v])
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
  ((.getUserObject widget) 1))
