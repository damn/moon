(ns moon.scroll-pane-cell
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.scroll-pane :as scroll-pane]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table :as gdx-table]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.widget-group :as widget-group]
            [moon.actor :as actor]
            [moon.table :as table]))

(defn create [skin viewport-height rows]
  (let [table (doto (gdx-table/create)
                (actor/set-name! "scroll-pane-table")
                (table/set-cell-defaults! {:pad 5})
                (table/add-rows! rows)
                (widget-group/pack!))]
    {:actor (doto (scroll-pane/create table skin)
              (actor/set-name! "moon.ui.widget.scroll-pane-table"))
     :width  (+ (actor/width table) 50)
     :height (min (- viewport-height 50)
                  (actor/height table))}))
