(ns stage.action-bar
  (:require [clojure.scenes.scene2d.actor.set-name :refer [set-name!]]
            [clojure.scenes.scene2d.actor.set-user-object :refer [set-user-object!]]
            [clojure.scenes.scene2d.ui.button-group :as button-group]
            [clojure.scenes.scene2d.ui.horizontal-group :as horizontal-group]
            [clojure.scenes.scene2d.utils.layout.set-fill-parent :refer [set-fill-parent!]]
            [gdx.scenes.scene2d.ui.table :as table]))

(defn create [_ctx]
  (doto (table/create
         {:table/cell-defaults {:pad 2}
          :table/rows [[{:actor (doto (horizontal-group/create
                                       {:space 2
                                        :pad 2})
                                  (set-name! "moon.ui.action-bar.horizontal-group")
                                  (set-user-object! (button-group/create
                                                     {:max-check-count 1
                                                      :min-check-count 0})))
                         :expand? true
                         :bottom? true}]]})
    (set-fill-parent! true)
    (set-name! "moon.ui.action-bar")))
