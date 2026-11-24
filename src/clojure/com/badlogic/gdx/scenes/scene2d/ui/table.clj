(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.table
  "A group that sizes and positions children using table constraints.
  
  Children added with [add!] (and similar methods returning a [[clojure.com.badlogic.gdx.scenes.scene2d.ui.cell]]) are laid out in rows and columns.
  Other children may be added with [clojure.com.badlogic.gdx.scenes.scene2d.group/add-actor!] (and similar methods) but are not laid out automatically and don't
  affect the preferred or minimum sizes.
  
  By default, get touchable is children only.
  
  The preferred and minimum sizes are that of the children laid out in columns and rows."
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn create []
  (Table.))

(defn add!
  "Adds a new cell with a label. This may only be called if a skin has been set with table or
  set skin."
  [^Table table ^Actor actor]
  (.add table actor))

(defn row!
  "Indicates that subsequent cells should be added to a new row and returns the cell values that will be used as the defaults
  for all cells in the new row."
  [^Table table]
  (.row table))

(defn defaults
  "The cell values that will be used as the defaults for all cells."
  [^Table table]
  (.defaults table))

(defn set-fill-parent! [^Table table flag?]
  (.setFillParent table flag?))
