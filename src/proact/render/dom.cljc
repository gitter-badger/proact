(ns proact.render.dom
  (:require [bbloom.vdom.core :as vdom]))

(defn tree->nodes
  ([x] (tree->nodes nil {} x))
  ([parent nodes {:keys [id children], tag :dom/tag, :as x}]
   (assert (nil? (nodes id)) (str "duplicate id: " id))
   (let [node (when tag
                {:id id
                 :tag tag
                 :props (:dom/props x)
                 :parent parent})
         node (if (= tag :text)
                (assoc node :text (:text x))
                (assoc node :children (mapv :id children)))
         parent (if node id parent)]
     (reduce (partial tree->nodes parent)
             (if node (assoc nodes id node) nodes)
             children))))

(defn tree->vdom [{:keys [id], mount :dom/mount, :as x}]
  {:post [(vdom/valid? %)]}
  (let [g (tree->nodes x)
        vdom (assoc vdom/null :nodes g :detached #{id})]
    (if mount
      (vdom/mount vdom mount id)
      vdom)))

(comment

  (require 'proact.examples.todo)
  (require 'proact.render.expand)
  (->
    (proact.examples.todo/app {})
    proact.render.expand/expand-all
    tree->vdom
    fipp.edn/pprint
    )

)
