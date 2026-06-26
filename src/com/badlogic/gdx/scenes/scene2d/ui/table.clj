(ns com.badlogic.gdx.scenes.scene2d.ui.table
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn create []
  (Table.))

(defn add [^Table table actor]
  (Table/.add table (actor/type-hint actor)))

(defn row [^Table table]
  (Table/.row table))

(defn defaults [^Table table]
  (Table/.defaults table))

(defn type-hint
  ^Table
  [obj]
  obj)
