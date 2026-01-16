(ns moon.schema
  (:require [moon.utils :as utils])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin)))

(defmulti create (fn [[schema-k :as _schema] v ctx]
                   schema-k))

(defmethod create :default
  [_ v {:keys [^Skin ctx/skin]}]
  (Label. (utils/truncate (utils/->edn-str v) 60) skin))

(defmulti value (fn [[schema-k :as _schema] widget schemas]
                  schema-k))

(defmethod value :default
  [_  widget _schemas]
  ((Actor/.getUserObject widget) 1))

(defmulti malli-form (fn [[k] _schemas]
                       k))

(defmulti create-value (fn [[k] _v _db]
                         k))

(defmethod create-value :default [_ v _db]
  v)
