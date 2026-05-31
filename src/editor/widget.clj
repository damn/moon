(ns editor.widget
  (:require [clojure.core-ext :refer [->edn-str
                                      truncate]]
            [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.ui.label :as label]))

(defmulti create (fn [[schema-k :as _schema] v ctx]
                   schema-k))

(defmulti value (fn [[schema-k :as _schema] widget schemas]
                  schema-k))

(defmethod create :default
  [_ v {:keys [ctx/skin]}]
  (label/create
   {:text (truncate (->edn-str v) 60)
    :skin skin}))

(defmethod value :default
  [_  widget _schemas]
  ((actor/user-object widget) 1))
