(ns moon.ui.widget
  (:require [gdl.ui.actor :as actor]
            [gdl.ui.label :as label]
            [gdl.ui.scroll-pane :as scroll-pane]
            [gdl.ui.stage :as stage]
            [moon.ui.table :as table]
            [moon.ui.text-button :as text-button]
            [moon.ui.window :as window]))

(defn- create-scroll-pane
  [{:keys [scroll-pane/actor
           actor/name]}
   skin]
  (doto (scroll-pane/create actor skin)
    (actor/set-name! name)))

(defn scroll-pane-cell [skin viewport-height rows]
  (let [table (table/create
               {:rows rows
                :actor/name "scroll-pane-table"
                :cell-defaults {:pad 5}
                :pack? true})]
    {:actor (create-scroll-pane
             {:actor/name "moon.ui.widget.scroll-pane-table"
              :scroll-pane/actor table}
             skin)
     :width  (+ (actor/width table) 50)
     :height (min (- viewport-height 50)
                  (actor/height table))}))

(defmethod stage/build :actor/scroll-pane-window
  [{:keys [skin viewport-height rows]}]
  (window/create
   {:skin skin
    :title "Choose"
    :modal? true
    :close-button? true
    :center? true
    :close-on-escape? true
    :rows [[(scroll-pane-cell skin viewport-height rows)]]
    :pack? true}))

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

(defn- v->actor [v skin]
  (if (map? v)
    (text-button/create
     {:text "Map"
      :on-clicked (fn [actor _ctx]
                    (stage/add-actor! (actor/stage actor)
                                      {:type :actor/data-viewer
                                       :title "title"
                                       :data v
                                       :width 500
                                       :height 500
                                       :skin skin}))
      :skin skin})
    (label/create (v->text v) skin)))

(defmethod stage/build :actor/data-viewer
  [{:keys [title
           data
           width
           height
           skin]}]
  {:pre [(map? data)]}
  (let [rows (for [[k v] (sort-by key data)]
               {:label (k->label-str k)
                :actor (v->actor v skin)})
        scroll-pane-table (table/create
                           {:rows (for [{:keys [label actor]} rows]
                                    [{:actor (label/create label skin)}
                                     {:actor actor}])})
        scroll-pane-cell (let [table (table/create
                                      {:rows [[scroll-pane-table]]
                                       :cell-defaults {:pad 1}
                                       :pack? true})]
                           {:actor (create-scroll-pane
                                    {:actor/name "dbg scroll pane"
                                     :scroll-pane/actor table}
                                    skin)
                            :width width ; (- (viewport/world-width viewport) 100) ; (+ 100 (/ (viewport/world-width viewport) 2))
                            :height height ; (- (viewport/world-height viewport) 200) ; (- (viewport/world-height viewport) 50) #_(min (- (:height viewport) 50) (height table))
                            })]
    (window/create {:skin skin
                    :title title
                    :close-button? true
                    :close-on-escape? true
                    :center? true
                    :rows [[scroll-pane-cell]]
                    :pack? true})))
