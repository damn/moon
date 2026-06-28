(ns scene2d.ui.window.add-close-button
  (:require [scene2d.utils.change-listener :as change-listener]
            [scene2d.ui.table.add-cell :refer [add-cell!]]
            [scene2d.ui.text-button :as text-button]
            [scene2d.ui.window.get-title-table :as get-title-table])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn f! [window skin]
  (add-cell! (get-title-table/f window)
             {:actor (doto (text-button/create {:text "X" :skin skin})
                       (Actor/.addListener (change-listener/create
                                            (fn [_event _actor]
                                              (Actor/.remove window)))))}))
