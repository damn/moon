(ns gdx.scenes.scene2d.ui.data-viewer-window
  (:require [gdx.scenes.scene2d.ui.data-viewer-window.v-text :refer [v->text]]
            [gdl.get-stage :refer [get-stage]]
            [gdl.add-listener :refer [add-listener!]]
            [gdl.label :as label]
            [gdl.scroll-pane :as scroll-pane]
            [gdl.window.add-close-button :as add-close-button]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdl.text-button :as text-button]
            [gdx.scenes.scene2d.ui.window :as window]
            [gdl.change-listener :as change-listener]
            [gdx.scenes.scene2d.ui.data-viewer-window.k-label-str :refer [k->label-str]]
            [gdl.stage.add-actor :refer [add-actor!]]))

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
                       (add-listener! (change-listener/create
                                       (fn [_event actor]
                                         (add-actor! (get-stage actor)
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
