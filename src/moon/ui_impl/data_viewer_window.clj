(ns moon.ui-impl.data-viewer-window
  (:require [moon.stage :as stage]
            [moon.ui :as ui])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defn- k->label-str [k]
  (str "[LIGHT_GRAY]:"
       (when-let [ns (namespace k)] (str ns "/"))
       "[][WHITE]"
       (name k)
       "[]"))

(defn- v->text [v]
  (cond
   (or (keyword? v)
       (number? v)
       (boolean? v)
       (string? v))
   (str "[GOLD]" v "[]")

   :else
   (str (class v))))

(declare create)

(defn- v->actor-decl [v skin]
  (if (map? v)
    {:type :ui/text-button
     :text "Map"
     :on-clicked (fn [actor _ctx]
                   (stage/add-actor! (Actor/.getStage actor)
                                     (create
                                      {:title "title"
                                       :data v
                                       :width 500
                                       :height 500
                                       :skin skin})))
     :skin skin}
    {:type :ui/label
     :label/text (v->text v)
     :label/skin skin}))

(defn create
  [{:keys [title
           data
           width
           height
           skin]}]
  {:pre [(map? data)]}
  (let [rows (for [[k v] (sort-by key data)]
               {:label (k->label-str k)
                :actor (ui/actor (v->actor-decl v skin))})
        scroll-pane-table (ui/actor
                           {:type :ui/table
                            :rows (for [{:keys [label actor]} rows]
                                    [{:actor (ui/actor
                                              {:type :ui/label
                                               :label/text label
                                               :label/skin skin})}
                                     {:actor actor}])})
        scroll-pane-cell (let [table (ui/actor
                                      {:type :ui/table
                                       :rows [[scroll-pane-table]]
                                       :cell-defaults {:pad 1}
                                       :pack? true})]
                           {:actor (ui/actor
                                    {:type :ui/scroll-pane
                                     :scroll-pane/actor table
                                     :scroll-pane/skin skin})
                            :width width ; (- (viewport/world-width viewport) 100) ; (+ 100 (/ (viewport/world-width viewport) 2))
                            :height height ; (- (viewport/world-height viewport) 200) ; (- (viewport/world-height viewport) 50) #_(min (- (:height viewport) 50) (height table))
                            })]
    (ui/actor
     {:type :ui/window
      :skin skin
      :title title
      :close-button? true
      :close-on-escape? true
      :center? true
      :rows [[scroll-pane-cell]]
      :pack? true})))
