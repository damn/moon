(ns gdx.scenes.scene2d.ui.data-viewer-window
  (:require [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.ui.label :as label]
            [com.badlogic.gdx.scenes.scene2d.ui.scroll-pane :as scroll-pane]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdx.scenes.scene2d.ui.text-button :as text-button]
            [gdx.scenes.scene2d.ui.window :as window]
            [gdx.stage :as stage]))

(defn create
  [{:keys [title
           data
           width
           height
           skin]}]
  {:pre [(map? data)]}
  (let [k->label-str (fn [k]
                       (str "[LIGHT_GRAY]:"
                            (when-let [ns (namespace k)] (str ns "/"))
                            "[][WHITE]"
                            (name k)
                            "[]"))
        v->text (fn [v]
                  (cond
                   (or (keyword? v)
                       (number? v)
                       (boolean? v)
                       (string? v))
                   (str "[GOLD]" v "[]")

                   :else
                   (str (class v))))
        v->actor (fn [v skin]
                   (if (map? v)
                     (text-button/create
                      {:text "Map"
                       :skin skin
                       :actor/listeners [[:listener/change
                                          (fn [_event actor]
                                            (stage/add-actor! (actor/stage actor)
                                                              (create
                                                               {:title "title"
                                                                :data v
                                                                :width 500
                                                                :height 500
                                                                :skin skin})))]]})
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
    (window/create
     {:title title
      :skin skin
      :table/rows [[scroll-pane-cell]]
      :window/close-button? skin})))
