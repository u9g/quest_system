completion_conditions should correspond to a static `Map<String, CompletionCondition>` where the key is the `type` and the rest of the fields on the completion condition are passed to the objectContext map as a `Map<String, String>`

rewards should correspond to a static `Map<String, RewardGiver>` where the key is the `type` and the rest of the fiuelds on the reward are passed to the rewardContext as a `Map<String, String>`

the quests will be held in a file called `QuestManager`

example quest file:

```yaml
questlines:
  - meta:
      id: harvest_wheat
      title: Harvest Wheat
      description: The first crop, but can you master it?
    quests:
      - meta:
          id: harvest_wheat_1
          title: Harvest Wheat 1
          description: Harvest 10 wheat
        completion_conditions:
          - type: break_crop
            target: wheat
            amount: 10
        rewards:
          - type: xp_bottle
            xp_amount: 100
          - type: seed
            target: wheat
            amount: 1
      - meta:
          id: harvest_wheat_2
          title: Harvest Wheat 2
          description: Harvest 20 wheat
        completion_conditions:
          - type: gather_item
            target: wheat
            amount: 20
        rewards:
          - type: xp_bottle
            xp_amount: 200
          - type: seed
            target: wheat
            amount: 3 # purposefully more than 5 to incentivize following quest line
      - meta:
          id: harvest_wheat_3
          title: Harvest Wheat 3
          description: Harvest 50 wheat
        completion_conditions:
          - type: gather_item
            target: wheat
            amount: 50
        rewards:
          - type: xp_bottle
            xp_amount: 200
          - type: seed
            target: wheat
            amount: 8
```