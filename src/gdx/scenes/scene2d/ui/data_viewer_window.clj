(ns gdx.scenes.scene2d.ui.data-viewer-window
  (:require [clojure.gdx.actor.add-listener :as add-listener]
            [clojure.gdx.actor.get-stage :as get-stage]
            [clojure.gdx.stage.add-actor :as add-actor]
            [gdx.scenes.scene2d.ui.data-viewer-window.v-text :refer [v->text]]
            [scene2d.ui.label :as label]
            [scene2d.ui.scroll-pane :as scroll-pane]
            [scene2d.ui.window.add-close-button :as add-close-button]
            [gdx.scenes.scene2d.ui.table :as table]
            [scene2d.ui.text-button :as text-button]
            [gdx.scenes.scene2d.ui.window :as window]
            [scene2d.utils.change-listener :as change-listener]
            [gdx.scenes.scene2d.ui.data-viewer-window.k-label-str :refer [k->label-str]]))

(defn create
  [{:keys [title
           data
           width
           height
           skin]}]
  {:pre [(map? data)]}
  (let [v->actor (fn [v skin]
                   (if (map? v)
                     (doto (text-button/create {:text "Map" :skin skin})
                       (add-listener/f (change-listener/create
                                        (fn [_event actor]
                                          (add-actor/f (get-stage/f actor)
                                                       (create
                                                        {:title "title"
                                                         :data v
                                                         :width 500
                                                         :height 500
                                                         :skin skin}))))))
                     (label/create
                      {:text (v->text v)
                       :skin skin})))
        rows (for [[k v] (sort-by key data)]
               {:label (k->label-str k)
                :actor (v->actor v skin)})
        scroll-pane-table (table/create
                           {:table/rows (for [{:keys [label actor]} rows]
                                          [{:actor (label/create {:text label
                                                                  :skin skin})}
                                           {:actor actor}])})

        ; TODO use moon.scroll-pane-cell
        scroll-pane-cell (let [table (table/create {:table/cell-defaults {:pad 1}
                                                    :table/rows [[scroll-pane-table]]})]
                           {:actor (scroll-pane/create
                                    {:actor table
                                     :skin skin})
                            :width width
                            ; (- (:viewport/world-width viewport) 100)
                            ; (+ 100 (/ (:viewport/world-width viewport) 2))
                            :height 800
                            ; (- (:viewport/world-height viewport) 200)
                            ; (- (:viewport/world-height viewport) 50) #_(min (- (:height viewport) 50) (height table))
                            })]
    (doto (window/create
           {:title title
            :skin skin
            :table/rows [[scroll-pane-cell]]})
      (add-close-button/f! skin))))
