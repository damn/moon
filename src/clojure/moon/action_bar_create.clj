(ns clojure.moon.action-bar-create
  (:require [clojure.actor.set-name :as set-name]
            [clojure.actor.set-user-object :as set-user-object]
            [clojure.button-group :as button-group]
            [clojure.horizontal-group :as horizontal-group]
            [clojure.set-fill-parent! :as set-fill-parent!]
            [clojure.ui-table :as table]))

(defn action-bar-create [_ctx]
  (doto (table/create
         {:table/cell-defaults {:pad 2}
          :table/rows [[{:actor (doto (horizontal-group/new)
                                  (horizontal-group/space! 2)
                                  (horizontal-group/pad! 2)
                                  (set-name/f "moon.ui.action-bar.horizontal-group")
                                  (set-user-object/f (doto (button-group/new)
                                                       (button-group/set-max-check-count! 1)
                                                       (button-group/set-min-check-count! 0))))
                         :expand? true
                         :bottom? true}]]})
    (set-fill-parent!/f true)
    (set-name/f "moon.ui.action-bar")))
